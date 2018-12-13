//var adminPath = 'http://10.6.97.10:8666/gw';
var adminPath = 'http://192.168.1.4:8666/gw';



function S4() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}
function guid() {
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}