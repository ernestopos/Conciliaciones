export interface DonutChartValue {
  total: number;
  statusName: string;
}

export interface DonutChartItem extends DonutChartValue {
  color: string;
}
