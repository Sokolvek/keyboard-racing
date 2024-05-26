import { createRouter, createWebHashHistory } from "vue-router";

import BrowseRooms from "./pages/BrowseRooms.vue"
import Main from "./pages/Main.vue"
import RoomPage from "./pages/RoomPage.vue"
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
        },
        {
            path:"/room/:id",
            component:RoomPage
        }
    ]
})

export default router