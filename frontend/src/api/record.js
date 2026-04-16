import request from '@/utils/request'

export function addRecord(data) {
  return request({
    url: '/record',
    method: 'post',
    data
  })
}

export function updateRecord(id, data) {
  return request({
    url: `/record/${id}`,
    method: 'put',
    data
  })
}

export function deleteRecord(id) {
  return request({
    url: `/record/${id}`,
    method: 'delete'
  })
}

export function getRecordPage(params) {
  return request({
    url: '/record/page',
    method: 'get',
    params
  })
}

export function getRecordList(params) {
  return request({
    url: '/record/list',
    method: 'get',
    params
  })
}
