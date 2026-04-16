import request from '@/utils/request'

export const userApi = {
    login(data) {
        return request({
            url: '/user/login',
            method: 'post',
            data
        })
    },
    register(data) {
        return request({
            url: '/user/register',
            method: 'post',
            data
        })
    },
    update(data) {
        return request({
            url: '/user/update',
            method: 'put',
            data
        })
    },
    getById(id) {
        return request({
            url: `/user/${id}`,
            method: 'get'
        })
    }
}

export const categoryApi = {
    list(userId) {
        return request({
            url: `/category/list/${userId}`,
            method: 'get'
        })
    },
    add(data) {
        return request({
            url: '/category/add',
            method: 'post',
            data
        })
    },
    update(data) {
        return request({
            url: '/category/update',
            method: 'put',
            data
        })
    },
    delete(id) {
        return request({
            url: `/category/${id}`,
            method: 'delete'
        })
    }
}

export const accountApi = {
    list(data) {
        return request({
            url: '/account/list',
            method: 'post',
            data
        })
    },
    add(data) {
        return request({
            url: '/account/add',
            method: 'post',
            data
        })
    },
    update(data) {
        return request({
            url: '/account/update',
            method: 'put',
            data
        })
    },
    delete(id) {
        return request({
            url: `/account/${id}`,
            method: 'delete'
        })
    },
    categoryStatistics(params) {
        return request({
            url: '/account/statistics/category',
            method: 'get',
            params
        })
    },
    trendStatistics(params) {
        return request({
            url: '/account/statistics/trend',
            method: 'get',
            params
        })
    },
    yearlyStatistics(params) {
        return request({
            url: '/account/statistics/yearly',
            method: 'get',
            params
        })
    }
}

export const budgetApi = {
    getByMonth(userId, month) {
        return request({
            url: `/budget/${userId}/${month}`,
            method: 'get'
        })
    },
    save(data) {
        return request({
            url: '/budget/save',
            method: 'post',
            data
        })
    }
}
