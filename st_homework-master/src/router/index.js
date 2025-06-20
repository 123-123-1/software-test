import { createRouter, createWebHistory } from 'vue-router'
import MainView from '../views/main/MainView.vue'
import Triangle from '../views/triangle/Triangle.vue'
import Calendar from '../views/calendar/Calendar.vue'
import Computer from '../views/computer/Computer.vue'
import Fee from '../views/fee/fee.vue'
import Sale from '../views/sale/sale.vue'
import test from '../views/test.vue'
import SystemTest from '../views/systemtest/SystemTest.vue'
import IntegrationTest from '../views/integrationtest/IntegrationTest.vue'

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
                path: '/integrationtest',
                name: 'integrationtest',
                component: IntegrationTest,
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
                path: '/unittest',
                name: 'UnitTest',
                component: () => import('../views/unittest/UnitTest.vue')
            },
            {
                path: '/triangle_wrong',
                name: 'TriangleWrong',
                component: () => import('@/views/triangle/TriangleWrong.vue')
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
