export function normalizeCollectionResponse<T>(response: unknown): T[] {
  if (Array.isArray(response)) {
    return response as T[];
  }

  const pageContent = (response as any)?.content;
  if (Array.isArray(pageContent)) {
    return pageContent as T[];
  }

  const dataContent = (response as any)?.data?.content;
  if (Array.isArray(dataContent)) {
    return dataContent as T[];
  }

  const dataArray = (response as any)?.data;
  if (Array.isArray(dataArray)) {
    return dataArray as T[];
  }

  return [];
}
