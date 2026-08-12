import { registerLocaleData } from '@angular/common';
import localeEsAr from '@angular/common/locales/es-AR';
import { ApplicationConfig, LOCALE_ID } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter } from '@angular/router';
import { definePreset } from '@primeuix/themes';
import Aura from '@primeuix/themes/aura';
import { ConfirmationService } from 'primeng/api';
import { providePrimeNG } from 'primeng/config';
import { routes } from './app.routes';
import { authInterceptor } from './core/auth/auth.interceptor';

registerLocaleData(localeEsAr);

const InclusoftPreset = definePreset(Aura, {
  semantic: { primary: { 50: '{blue.50}', 100: '{blue.100}', 200: '{blue.200}', 300: '{blue.300}', 400: '{blue.400}', 500: '{blue.500}', 600: '{blue.600}', 700: '{blue.700}', 800: '{blue.800}', 900: '{blue.900}', 950: '{blue.950}' } }
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideAnimationsAsync(),
    { provide: LOCALE_ID, useValue: 'es-AR' },
    ConfirmationService,
    providePrimeNG({ theme: { preset: InclusoftPreset, options: { darkModeSelector: false } }, ripple: true, translation: { firstDayOfWeek: 1, dayNames: ['domingo','lunes','martes','miércoles','jueves','viernes','sábado'], dayNamesShort: ['dom','lun','mar','mié','jue','vie','sáb'], dayNamesMin: ['D','L','M','X','J','V','S'], monthNames: ['enero','febrero','marzo','abril','mayo','junio','julio','agosto','septiembre','octubre','noviembre','diciembre'], monthNamesShort: ['ene','feb','mar','abr','may','jun','jul','ago','sep','oct','nov','dic'], today: 'Hoy', clear: 'Limpiar' } }),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideRouter(routes)
  ]
};
