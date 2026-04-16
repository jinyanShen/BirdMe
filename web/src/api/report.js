import request from '@/utils/request'

// Query list
export function pageReport(query) {
  return request({
    url: '/report/page',
    method: 'post',
    data: query
  })
}

export function pageMyReport(query) {
  return request({
    url: '/report/pageMy',
    method: 'post',
    data: query
  })
}

// Query all list
export function allReport() {
  return request({
    url: '/report/all',
    method: 'get'
  })
}

// Query by status
export function getReportByStatus(status) {
  return request({
    url: '/report/by-status',
    method: 'get',
    params: { status: status}
  })
}

// Query by submitter ID
export function getReportsBySubmitter(submitterId) {
  return request({
    url: '/report/by-submitter',
    method: 'get',
    params: { submitterId: submitterId}
  })
}

// Query details
export function getReport(id) {
  return request({
    url: '/report/getOne',
    method: 'get',
    params: { id: id}
  })
}

// Add new
export function insertReport(data) {
  return request({
    url: '/report/insert',
    method: 'post',
    data: data
  })
}

// Update
export function updateReport(data) {
  return request({
    url: '/report/update',
    method: 'post',
    data: data
  })
}

// Delete
export function delReport(ids) {
  return request({
    url: '/report/delete',
    method: 'post',
    data: ids
  })
}

// Update status
export function updateReportStatus(data) {
  return request({
    url: '/report/update-status',
    method: 'post',
    data: data
  })
}

// Submit new report
export function submitReport(data) {
  return request({
    url: '/report/submit',
    method: 'post',
    data: data
  })
}
