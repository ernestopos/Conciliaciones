export interface CarrierPortal {
  id: number;
  carrierId: number | null;
  carrierCode: string | null;
  carrierName: string | null;
  code: string;
  displayName: string;
  portalUrl: string;
  logoUrl: string | null;
  description: string | null;
  allowUpload: boolean;
  allowDownload: boolean;
  requiresMfa: boolean;
}
