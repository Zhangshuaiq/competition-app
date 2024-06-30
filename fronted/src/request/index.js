import axios from "axios";
import baseUrl from "@/config";

const axiosInstance = axios.create({
    baseURL: baseUrl
});
// axiosInstance.interceptors.request.use((config) => {
//     let token =  window.sessionStorage.getItem('token')
//     if (token) {
//         config.headers.token = window.sessionStorage.getItem('token')
//     }
//     return config;
// });

axiosInstance.interceptors.response.use(
    response => {
        if (response && response.data) {
            console.log(response)
            let data = response.data;
            return data;
        }
        return response;
    },
    error => {
        console.log(error)
        sessionStorage.clear()
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
