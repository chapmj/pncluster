

# Map reduce projct
Deploy Akka clustering map reduce application to docker	

## Docker runtime

### Take any image, create a container, then start the container
`docker run <image>`

### Start a container
`docker start <name -or- id>`

### Stop a container
`docker stop <name -or- id>`
	
### List running containers
`docker ps`

### List running and stopped containers
`docker ps -a`

### Delete a container
`docker rm <name -or- id>`

### port forward docker vm port 80 to host 8080
`docker run -p 8080:80 tutum/hello-world`

### name a docker container
`docker run -d --name web1 -p 8080:80 tutum/hello-world`

##  Docker Build
Place a file named Dockerfile in the target directory.
"." is recursively added to the docker container
-f /path is used to point to a filesystem
-t specifices a repo to store the docker image
`docker build -f /path/to/Dockerfile .`

### Docker compose
todo

## Akka Clustering
Goal: start up Akka cluster on docker.

Components of cluster:

Docker container starts and launched cluster member
Seed0 launches first to coordiante membership
Seed1 also launches.

In the start code, we have a client that launches routers which coordinate work
to server actor systems.

What I would like to do is use a random router to distribute messaging. This is
to simplify the selection process.

Work still continues as follows.  Mapper Jobs => Mappers until all mappers have
completed their work. Then Reducer Jobs => reducers
