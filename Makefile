.DEFAULT_GOAL := help
.SILENT: format-code

format-code: ## Code formatter with Google Style.
	echo "Formatting code with google style ..."
	find . | grep '\.java' | xargs java -jar bin/google-java-format-1.7-all-deps.jar -r

help: ## Show this help.
	@fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//' | sed -e 's/##//'

babel-translations: ## Updates the translations / Generate the JAR and updates keys.pot
	echo "Translations status:"
	./gradlew i18n-status
	echo "Getting messages used in the project"
	./gradlew i18n-gettext
	echo "Uploading pending translations to Babel"
	./gradlew i18n-upload
	echo "Downloading new translations from Babel"
	./gradlew i18n-download
	echo "Packaging translations into a deployable JAR"
	./gradlew i18n-makemo
