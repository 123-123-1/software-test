import { createRouter, createWebHistory } from 'vue-router'
import MainView from '../views/main/MainView.vue'
import Triangle from '../views/triangle/Triangle.vue'
import Calendar from '../views/calendar/Calendar.vue'
import Computer from '../views/computer/Computer.vue'
import Fee from '../views/fee/fee.vue'
import Sale from '../views/sale/sale.vue'
import test from '../views/test.vue'
import test2 from '../views/test2.vue'
import SystemTest from '../views/systemtest/SystemTest.vue'

const routes = [
    {
        path: '/',
        component: MainView,
        children: [
            {
                path: '/triangle',
                name: 'triangle',
                component: Triangle,
            },
            {
                path: '/calendar',
                name: 'calendar',
                component: Calendar,
            },
            {
                path: '/computer',
                name: 'computer',
                component: Computer,
            },
            {
                path: '/systemtest',
                name: 'systemtest',
                component: SystemTest,
            },
            {
                path: '/fee',
                name: 'fee',
                component: Fee,
            },
            {
                path: '/sale',
                name: 'sale',
                component: Sale,
            },
            {
                path: '/test',
                name: 'test',
                component: test
            },
            {
                path: '/test2',
                name: 'test2',
                component: test2
            }
        ]
    },
    {
        path: '/',
        name: 'Home',
        component: () => import('../views/Home.vue')
    },
    {
        path: '/systemtest',
        name: 'SystemTest',
        component: () => import('../views/systemtest/SystemTest.vue')
    },
    {
        path: '/unittest',
        name: 'UnitTest',
        component: () => import('../views/unittest/UnitTest.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
