// src/api/injury.js

import request from '@/utils/request'

/**
 * Get injury report heatmap data
 * @param {Object} params - Query parameters
 * @param {string} params.timeRange - Time range: 7,30,90,all
 * @param {string} params.species - Filter by bird species
 * @param {string} params.injuryType - Filter by injury type
 */
export function getInjuryHeatmap(params) {
  return request({
    url: '/api/injury/heatmap',
    method: 'get',
    params
  })
}

/**
 * Get recent injury reports list
 * @param {Object} params - Query parameters
 * @param {number} params.limit - Number of records to return
 * @param {number} params.offset - Pagination offset
 */
export function getRecentReports(params) {
  return request({
    url: '/api/injury/recent',
    method: 'get',
    params
  })
}

/**
 * Get injury report details by ID
 * @param {number} id - Report ID
 */
export function getReportDetail(id) {
  return request({
    url: `/api/injury/report/${id}`,
    method: 'get'
  })
}

/**
 * Get paginated list of all injury reports
 * @param {Object} params - Query parameters
 */
export function getReportList(params) {
  return request({
    url: '/api/injury/reports',
    method: 'get',
    params
  })
}
