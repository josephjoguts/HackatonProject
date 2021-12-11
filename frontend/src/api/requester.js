//import axios from "axios";

export default class Requester {

    static POST_METHOD = 'post'
    static PUT_METHOD = 'put'
    static GET_METHOD = 'get'
    static DELETE_METHOD = 'delete'
    static HEADERS = {
        "accept": "application/json",
        "Content-Type": "application/json",
    }
    // Example POST method implementation:
    static async postData(url = '', data = {}) {
        // Default options are marked with *
        const response = await fetch(url, {
            method: Requester.POST_METHOD,
            mode: 'cors', // no-cors, *cors, same-origin
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            credentials: 'same-origin', // include, *same-origin, omit
            headers: {
                'Content-Type': 'application/json'
                // 'Content-Type': 'application/x-www-form-urlencoded',
            },
            redirect: 'follow', // manual, *follow, error
            referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
            body: JSON.stringify(data) // body data type must match "Content-Type" header
        });
        return response.json(); // parses JSON response into native JavaScript objects
    }

    /*
    static _formRequestConfig(method, url, data = {}) {
        return {
            method,
            url,
            data,
            params: method === this.GET_METHOD ? data : null,
            headers: this.#HEADERS,
            responseType: 'json',
        }
    }

    static async basicRequest(method, url, data = {}) {
        try {
            const response = await axios(this._formRequestConfig(method, url, data))
            console.log(response)
            return response
        } catch (e) {
            console.log(e)
            return e
        }
    }*/

}
