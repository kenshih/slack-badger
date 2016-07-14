host_name := "mup.cr"
repo_name := "badger-ken"
project_name := "webserver"
project_dir := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
BUILD_NUMBER ?= 1

# lists all available targets
list:
	@sh -c "$(MAKE) -p no_targets__ | \
		awk -F':' '/^[a-zA-Z0-9][^\$$#\/\\t=]*:([^=]|$$)/ {split(\$$1,A,/ /);\
		for(i in A)print A[i]}' | \
		grep -v '__\$$' | \
		grep -v 'make\[1\]' | \
		grep -v 'Makefile' | \
		sort"
# required for list
no_targets__:

package:
	docker build -t $(host_name)/$(repo_name)/$(project_name):$(BUILD_NUMBER) .

run:
	@docker run -it -P --rm \
		-h $(project_name) \
		--name $(project_name)_run \
		$(host_name)/$(repo_name)/$(project_name)

test:
	echo "No tests yet!!!"

term:
	@docker run -it --rm \
		-h $(project_name) \
		--name $(project_name)_term \
		$(host_name)/$(repo_name)/$(project_name) /bin/bash -i

root-term:
	@docker run -it --rm \
		-h $(project_name) \
		-v $(HOME):/root \
		--name $(project_name)_root-term \
		-u="root" \
		$(host_name)/$(repo_name)/$(project_name) /bin/bash -i

publish:
	docker push $(host_name)/$(repo_name)/$(project_name):$(BUILD_NUMBER)

deploy:
	echo "No deploy steps written"

docs:
	@cat README.md
