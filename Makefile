.DEFAULT_GOAL := help
.SILENT: format-code

format-code: ## Code formatter with Google Style.
	echo "Formatting code with google style ..."
	find . | grep '\.java' | xargs java -jar bin/google-java-format-1.7-all-deps.jar -r

help: ## Show this help.
	@fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//' | sed -e 's/##//'
