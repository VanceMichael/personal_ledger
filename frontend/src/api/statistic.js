import request from '@/utils/request'

export function setBudget(data) {
  return request({
    url: '/statistic/budget',
    method: 'post',
    data
  })
}

export function getBudget(params) {
  return request({
    url: '/statistic/budget',
    method: 'get',
    params
  })
}

export function getMonthStatistic(params) {
  return request({
    url: '/statistic/month',
    method: 'get',
    params
  })
}

export function getYearStatistic(params) {
  return request({
    url: '/statistic/year',
    method: 'get',
    params
  })
}
