import api from "../../../shared/services/api";
import { CreateSubscriptionRequest, SubscriptionResponse } from "../types/membership.types";

const SUBSCRIPTION_ENDPOINT = "/subscriptions";

export const subscriptionService = {
  /**
   * Crea una nueva suscripción para un socio
   */
  createSubscription: async (request: CreateSubscriptionRequest): Promise<SubscriptionResponse> => {
    const response = await api.post<SubscriptionResponse>(SUBSCRIPTION_ENDPOINT, request);
    return response.data;
  },

  /**
   * Obtiene la suscripción activa de un socio
   */
  getActiveByMember: async (memberId: number): Promise<SubscriptionResponse | null> => {
    try {
      const response = await api.get<SubscriptionResponse>(`${SUBSCRIPTION_ENDPOINT}/active`, {
        params: { memberId }
      });
      return response.data;
    } catch (err) {
      return null;
    }
  }
};
