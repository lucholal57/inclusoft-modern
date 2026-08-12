package ar.org.inclusoft.api.student;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.org.inclusoft.api.InclusoftApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = InclusoftApiApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentRepository studentRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createsAndSearchesStudents() throws Exception {
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Ana",
                                  "lastName": "Gómez",
                                  "documentNumber": "40111222",
                                  "birthDate": "2001-04-12"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/api/v1/students").param("search", "gómez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentNumber").value("40111222"));
    }

    @Test
    void acceptsCrossOriginPreflightForCrudMethods() throws Exception {
        mockMvc.perform(options("/api/v1/students/00000000-0000-0000-0000-000000000000")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "PUT"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PUT")))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PATCH")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void replacesStudentContactsThroughPut() throws Exception {
        Student student = studentRepository.save(new Student("Luciana", "Pérez", "40555666", null, null, null, null));

        mockMvc.perform(put("/api/v1/students/{id}/contacts", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{
                                  "fullName": "María Pérez",
                                  "relationship": "Madre",
                                  "phoneNumber": "2964000000",
                                  "email": "maria@example.com",
                                  "responsible": true,
                                  "emergencyContact": true
                                }]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("María Pérez"))
                .andExpect(jsonPath("$[0].responsible").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void storesStudentHealthRecordThroughPut() throws Exception {
        Student student = studentRepository.save(new Student("Tomás", "Pérez", "40123456", null, null, null, null));

        mockMvc.perform(put("/api/v1/students/{id}/health", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "medicalReferences": "Información comunicada por la familia",
                                  "medications": "Medicación según indicación médica",
                                  "allergies": "Sin alergias informadas",
                                  "healthInsurance": "Obra social",
                                  "treatingProfessionals": "Profesional externo",
                                  "supportGuidelines": "Acompañamiento en actividades",
                                  "observations": "Actualizar ante cambios"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.healthInsurance").value("Obra social"));

        mockMvc.perform(get("/api/v1/students/{id}/health", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supportGuidelines").value("Acompañamiento en actividades"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void storesStudentAuthorizationsThroughPut() throws Exception {
        Student student = studentRepository.save(new Student("Sofía", "Pérez", "40234567", null, null, null, null));

        mockMvc.perform(put("/api/v1/students/{id}/authorizations", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "imageUseAuthorized": true,
                                  "localOutingsAuthorized": true,
                                  "medicalEmergencyAuthorized": false,
                                  "dataSharingAuthorized": false,
                                  "authorizedBy": "María Pérez, madre",
                                  "authorizationDate": "2026-08-12",
                                  "observations": "Constancia archivada en legajo"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUseAuthorized").value(true))
                .andExpect(jsonPath("$.authorizedBy").value("María Pérez, madre"));
    }
}
