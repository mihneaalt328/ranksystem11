# RankSystem 1.0.1

Custom rank + permission system for Spigot 1.8.9.

## Commands
/rank help
/rank create <rank>
/rank delete <rank>
/rank set <player> <rank>
/rank remove <player>
/rank list
/rank info <rank>
/rank prefix <rank> <prefix>
/rank suffix <rank> <suffix>
/rank priority <rank> <number>
/rank inherit <rank> <parent>
/rank uninherit <rank> <parent>
/rank permission <rank> add <permission>
/rank permission <rank> remove <permission>
/rank permission <rank> list
/rank reload

Aliases:
/ranks
/setrank <player> <rank>
/setperms <rank> <add|remove|list> [permission]

## Build without installing anything

1. Create a GitHub repository.
2. Upload this project.
3. Open Actions.
4. Select "Build RankSystem".
5. Press "Run workflow" (or push any change; it builds automatically).
6. Open the completed workflow run.
7. Download the artifact named "RankSystem-1.0.1".
8. Put RankSystem-1.0.1.jar into your server's plugins folder.

Requires no Java, Maven, IntelliJ, or other software on your PC.
