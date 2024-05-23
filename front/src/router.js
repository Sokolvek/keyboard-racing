import { createRouter, createWebHashHistory } from "vue-router";

import BrowseRooms from "./pages/BrowseRooms.vue"
import Main from "./pages/Main.vue"
const router = createRouter({
    history:createWebHashHistory(),
    routes:[
        {
          path:"/",
          component:Main
        },
        {
            path:"/rooms",
            component:BrowseRooms
        }
    ]
})

export default router