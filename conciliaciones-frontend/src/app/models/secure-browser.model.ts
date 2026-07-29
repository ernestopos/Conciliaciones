export interface SecureBrowserLaunchRequest {
  carrierPortalId: number;
}

export interface SecureBrowserLaunchResponse {
  carrierPortalId: number;
  carrierCode: string | null;
  carrierName: string | null;
  portalCode: string;
  portalName: string;
  launchUrl: string;
  auditReference: string;
}
