/**
 * Created by 钱斌 on 2016/8/12.
 */
var routerApp = angular.module('routerApp', ['ui.router', 'taskModule']);

routerApp.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/area');
    $stateProvider
        /*没有参数的跳转*/
        .state('area', {
            url: '/area',
            views: {
                '': {
                    templateUrl: 'div/main.html'
                },
                'topbar@area': {
                    templateUrl: 'div/topbar.html'
                },
                'main@area': {
                    templateUrl: 'div/area.html'
                }
            }
        })
        .state('cell', {
            url: '/cell',
            views: {
                '': {
                    templateUrl: 'div/main.html'
                },
                'topbar@cell': {
                    templateUrl: 'div/topbar.html'
                },
                'main@cell': {
                    templateUrl: 'div/cell.html'
                }
            }
        })
        .state('poi', {
            url: '/poi',
            views: {
                '': {
                    templateUrl: 'div/main.html'
                },
                'topbar@poi': {
                    templateUrl: 'div/topbar.html'
                },
                'main@poi': {
                    templateUrl: 'div/poi.html'
                }
            }
        })
        .state('form', {
            url: '/form',
            views: {
                '': {
                    templateUrl: 'div/main.html'
                },
                'topbar@form': {
                    templateUrl: 'div/topbar.html'
                },
                'main@form': {
                    templateUrl: 'div/form.html'
                }
            }
        });
});


