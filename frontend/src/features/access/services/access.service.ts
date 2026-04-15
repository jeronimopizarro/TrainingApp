import api from "../../../shared/services/api";
import { GymAccessSummaryResponse, QrTokenResponse, ValidateAccessRequest, ValidateAccessResponse } from "../types/access.types";

const ACCESS_ENDPOINT = "/access";

export const accessService = {
  /**
   * Obtiene el historial de accesos del gimnasio con filtro opcional de estado
   */
  getAccessLogs: async (granted?: boolean): Promise<GymAccessSummaryResponse> => {
    const params = granted !== undefined ? { granted } : {};
    const response = await api.get<GymAccessSummaryResponse>(`${ACCESS_ENDPOINT}/logs`, { params });
    return response.data;
  },

  /**
   * Valida un acceso por DNI o QR
   */
  validateAccess: async (request: ValidateAccessRequest): Promise<ValidateAccessResponse> => {
    const response = await api.post<ValidateAccessResponse>(`${ACCESS_ENDPOINT}/validate`, request);
    return response.data;
  },

  /**
   * Genera un token QR para un socio
   */
  generateQr: async (memberId: number): Promise<QrTokenResponse> => {
    const response = await api.get<QrTokenResponse>(`${ACCESS_ENDPOINT}/qr`, { params: { memberId } });
    return response.data;
  }
};
