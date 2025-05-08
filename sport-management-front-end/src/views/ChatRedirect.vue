<script setup>
import { useRoute } from 'vue-router';
import { useRouter } from 'vue-router';
import { onMounted } from 'vue';

const router = useRouter();
const route = useRoute();

onMounted(() => {
  router.push({
    path: '/chats/list',
    query: route.query
  })
});

</script>

<template>

</template>