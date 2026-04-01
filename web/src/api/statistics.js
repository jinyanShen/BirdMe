import request from '@/utils/request'

// Get report statistics
export function getReportStatistics() {
  return request({
    url: '/statistics/report',
    method: 'get'
  })
}

// Get reports by status
export function getReportsByStatus() {
  return request({
    url: '/statistics/reports-by-status',
    method: 'get'
  })
}

// Get reports by injury type
export function getReportsByInjuryType() {
  return request({
    url: '/statistics/reports-by-injury-type',
    method: 'get'
  })
}

// Get reports over time
export function getReportsOverTime() {
  return request({
    url: '/statistics/reports-over-time',
    method: 'get'
  })
}

// Get rescue station statistics
export function getRescueStationStatistics() {
  return request({
    url: '/statistics/rescue-station',
    method: 'get'
  })
}

// Get overall statistics
export function getOverallStatistics() {
  return request({
    url: '/statistics/overall',
    method: 'get'
  })
}