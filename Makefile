
clean:
	lein clean

prod-build: clean
	lein cljsbuild once min

deploy: prod-build
	firebase deploy
