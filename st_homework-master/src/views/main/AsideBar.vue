<template>
    <el-menu class="el-menu-vertical-demo" :collapse="isCollapse" router background-color="#343c44" text-color="#ffffff"
        active-text-color="#ffd700" :default-openeds="openeds">
        <el-sub-menu index="1">
            <template v-slot:title>
                <span style="padding-left:20px">课程练习</span>
            </template>
            <el-menu-item-group style="flex-grow:1">
                <el-menu-item index="/triangle" style="justify-content:start">
                    1.判断三角形类型
                </el-menu-item>
                <el-menu-item index="/triangle_wrong"style="justify-content:start">
        
                    判断三角形类型（BUG）
                </el-menu-item>
                <el-menu-item index="/calendar" style="justify-content:start">
                    2.万年历问题
                </el-menu-item>
                <el-menu-item index="/computer" style="justify-content:start">
                    3.电脑销售
                </el-menu-item>
                <el-menu-item index="/fee" style="justify-content:start">
                    4.话费系统
                </el-menu-item>
                <el-menu-item index="/sale" style="justify-content:start">
                    5.销售平台
                </el-menu-item>
            </el-menu-item-group>
        </el-sub-menu>
        <el-sub-menu index="2">
            <template v-slot:title>
                <span style="padding-left:20px">项目测试</span>
            </template>
            <el-menu-item-group style="flex-grow:1">
                <el-menu-item index="/unittest" style="justify-content:start">
                    1.单元测试
                </el-menu-item>
                <el-menu-item index="/calendar" style="justify-content:start">
                    2.集成测试
                </el-menu-item>
                <el-menu-item index="/systemtest" style="justify-content:start">
                    3.系统测试
                </el-menu-item>
            </el-menu-item-group>
        </el-sub-menu>
    </el-menu>
</template>
<script lang="ts" setup>
import { ref } from 'vue'


const props = defineProps({
    isCollapse: Boolean
})
const openeds = ref(['1'])

</script>

<style>
.el-menu-vertical-demo:not(.el-menu--collapse) {
    border-right: 0px;
    width: 200px;
    height: 100%;
}

.el-menu-vertical-demo.el-menu--collapse {
    border-right: 0px;
    min-height: 100%;
}
</style>