rootProject.name = "BiniyogBuddy"

include(
    "common",
    "libs:users",
    "libs:auth",
    "libs:stocks",
    "libs:trades",
    "apps:api-app"
)


include("libs:scraper")
include("libs:market")