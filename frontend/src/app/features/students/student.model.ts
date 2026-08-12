export interface Student {
  id: string;
  firstName: string;
  lastName: string;
  documentNumber: string;
  phoneNumber: string | null;
  birthDate: string | null;
  birthPlace: string | null;
  address: string | null;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface CreateStudent {
  firstName: string;
  lastName: string;
  documentNumber: string;
  phoneNumber?: string | null;
  birthDate?: string | null;
  birthPlace?: string | null;
  address?: string | null;
}

export interface StudentContact {
  id: string;
  fullName: string;
  relationship: string;
  phoneNumber: string;
  email: string | null;
  responsible: boolean;
  emergencyContact: boolean;
}

export interface StudentContactRequest {
  fullName: string;
  relationship: string;
  phoneNumber: string;
  email: string | null;
  responsible: boolean;
  emergencyContact: boolean;
}

export interface StudentHealthRecord {
  medicalReferences: string | null;
  medications: string | null;
  allergies: string | null;
  healthInsurance: string | null;
  treatingProfessionals: string | null;
  supportGuidelines: string | null;
  observations: string | null;
}

export interface StudentAuthorization {
  imageUseAuthorized: boolean;
  localOutingsAuthorized: boolean;
  medicalEmergencyAuthorized: boolean;
  dataSharingAuthorized: boolean;
  authorizedBy: string | null;
  authorizationDate: string | null;
  observations: string | null;
}
