import axios from "axios";
import baseUrl from "@/config";

const axiosInstance = axios.create({
    baseURL: baseUrl
});

// 标记后台是否曾经断开过
let backendWasDown = false;

axiosInstance.interceptors.response.use(
    response => {
        // 后台从断开恢复：清空旧状态并跳回登录页
        if (backendWasDown) {
            backendWasDown = false;
            sessionStorage.clear();
            window.location.href = '/';
            return;
        }
        if (response && response.data) {
            return response.data;
        }
        return response;
    },
    error => {
        backendWasDown = true;
        return Promise.reject(error);
    }
);

const dataHandle = response => {
    return response;
};

const innerGet = function() {
    return axiosInstance.get.apply(this, arguments).then(dataHandle);
};

const innerPost = function() {
    return axiosInstance.post.apply(this, arguments).then(dataHandle);
};

const innerDelete = function() {
    return axiosInstance.delete.apply(this, arguments).then(dataHandle);
};

const innerPut = function() {
    return axiosInstance.put.apply(this, arguments).then(dataHandle);
};

export const pureGet = function(url) {
    return innerGet(url);
};

export const pureDelete = function(url) {
    return innerDelete(url);
};

export const get = function(url, params) {
    return innerGet(url, {
        params: params
    });
};

export const post = function(url, data) {
    return innerPost(url, data, {
        headers: {
            "Content-Type": "application/json"
        }
    });
};

export const del = function(url, params) {
    return innerDelete(url, {
        params: params
    });
};

export const put = function(url, data) {
    return innerPut(url, data, {
        headers: {
            "Content-Type": "application/json"
        }
    });
};
