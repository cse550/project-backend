APP_NAME?=project-backend

_GIT_BRANCH:=$(shell git rev-parse --abbrev-ref HEAD)
_GIT_HASH:=$(shell git rev-parse --short HEAD)
_GIT_PREFIX=${_GIT_BRANCH}_${_GIT_HASH}

TIME:=$(shell date +%s)
# "L" prefix here refers to "local" to explicitly differentiate local builds from future
# automated/CI builds
BUILD_STRING?=L${TIME}

DOCKER_TAG?=${_GIT_PREFIX}_${BUILD_STRING}

_DC_FLAGS=
DC_EXTRA_FLAGS?=
DC_FLAGS?=${_DC_FLAGS} ${DC_EXTRA_FLAGS}


default: image

.PHONY: image
image:
	PROJECT_BACKEND_TAG=${DOCKER_TAG} docker-compose -f ./compose.yaml build ${DC_FLAGS} spring-boot-app


.PHONY: image_tar
image_tar: image
	docker image save project-backend:latest -o ./project-backend_${DOCKER_TAG}.tar
