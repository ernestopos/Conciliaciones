import { CommonModule } from '@angular/common';
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NgApexchartsModule } from 'ng-apexcharts';
import {
  ApexChart,
  ApexDataLabels,
  ApexFill,
  ApexLegend,
  ApexPlotOptions,
  ApexResponsive,
  ApexStroke,
  ApexTooltip
} from 'ng-apexcharts';
import { DonutChartItem } from '../../../models/donut-chart.model';

export type DonutSummaryChartOptions = {
  series: number[];
  chart: ApexChart;
  labels: string[];
  colors: string[];
  dataLabels: ApexDataLabels;
  legend: ApexLegend;
  plotOptions: ApexPlotOptions;
  stroke: ApexStroke;
  fill: ApexFill;
  tooltip: ApexTooltip;
  responsive: ApexResponsive[];
};

@Component({
  selector: 'app-donut-summary-chart',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    NgApexchartsModule
  ],
  templateUrl: './donut-summary-chart.component.html',
  styleUrl: './donut-summary-chart.component.scss'
})
export class DonutSummaryChartComponent implements OnChanges {
  @Input({ required: true }) title = '';
  @Input() subtitle = 'Distribución por estado';
  @Input() icon = 'donut_large';
  @Input() iconTone: 'blue' | 'green' = 'blue';
  @Input() items: DonutChartItem[] = [];
  @Input() loading = false;
  @Input() errorMessage = '';

  total = 0;
  chartOptions: DonutSummaryChartOptions = this.createChartOptions();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['items']) {
      this.total = this.items.reduce((sum, item) => sum + Number(item.total || 0), 0);
      this.chartOptions = this.createChartOptions();
    }
  }

  percentage(value: number): number {
    return this.total > 0 ? Math.round((value / this.total) * 100) : 0;
  }

  formatNumber(value: number): string {
    return new Intl.NumberFormat('es-CO').format(value);
  }

  private createChartOptions(): DonutSummaryChartOptions {
    const series = this.items.map((item) => Number(item.total || 0));
    const labels = this.items.map((item) => item.statusName);
    const colors = this.items.map((item) => item.color);

    return {
      series,
      labels,
      colors,
      chart: {
        type: 'donut',
        height: 390,
        fontFamily: 'Roboto, Helvetica Neue, sans-serif',
        animations: {
          enabled: true,
          speed: 700
        },
        toolbar: {
          show: false
        }
      },
      dataLabels: {
        enabled: true,
        formatter: (value: number) => `${Math.round(value)}%`,
        style: {
          fontSize: '16px',
          fontWeight: '700',
          colors: ['#ffffff']
        },
        dropShadow: {
          enabled: false
        }
      },
      legend: {
        show: false
      },
      plotOptions: {
        pie: {
          expandOnClick: false,
          donut: {
            size: '48%',
            labels: {
            show: true,
            name: {
              show: true
            },
            value: {
              show: true
            },
            total: {
              show: true,
              showAlways: true,
              label: 'Total',
              color: '#64748b',
              fontSize: '16px',
              fontWeight: 500,
              formatter: (): string => {
                return new Intl.NumberFormat('es-CO').format(this.total);
              }
            }
          }
          }
        }
      },
      stroke: {
        show: true,
        width: 2,
        colors: ['#ffffff']
      },
      fill: {
        type: 'gradient',
        gradient: {
          shade: 'light',
          type: 'diagonal1',
          shadeIntensity: 0.18,
          opacityFrom: 1,
          opacityTo: 0.92,
          stops: [0, 100]
        }
      },
      tooltip: {
        y: {
          formatter: (value: number) => new Intl.NumberFormat('es-CO').format(value)
        }
      },
      responsive: [
        {
          breakpoint: 1400,
          options: {
            chart: {
              height: 340
            },
            dataLabels: {
              style: {
                fontSize: '14px'
              }
            }
          }
        },
        {
          breakpoint: 700,
          options: {
            chart: {
              height: 310
            }
          }
        }
      ]
    };
  }
}
