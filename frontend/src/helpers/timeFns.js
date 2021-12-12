function toTimestamp(strDate){
    var datum = Date.parse(strDate);
    return datum/1000
}

function toTime(timestamp){
    return new Date(timestamp * 1000)
}

function pad(elem, n) {
    //for (var r = elem.toString(); r.length < n; r = 0 + r);
    let r = elem.toString()
    if (r.length < n) {
        for ( let i = 0; i < (n - r.length); i++) {
            r = '0' + r
        }
    }
    return r
}

export { toTime, toTimestamp, pad }
