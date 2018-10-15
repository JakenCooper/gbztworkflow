require.config({
    baseUrl:'/gw/static',
    paths:{
        'jquery':['jquery/jquery-3.3.1'],
        'jquery.form':['jquery/jquery.form'],
        'bootstrap':['bootstrap3/js/bootstrap']
    },
    shim:{
        'jquery.form':{
            deps:'jquery',
            exports:'jQuery.fn.form'
        },
        'bootstrap':{
            deps:'jQuery',
            exports:'$'
        }
    }
});

var adminPath = '/gw';