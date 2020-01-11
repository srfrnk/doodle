FORCE:

consume-london-police-crimes: FORCE
	clear
	gradle -p consumers/london-police run --args="crimes"

consume-london-police-boundaries: FORCE
	clear
	gradle -p consumers/london-police run --args="boundaries"

start-db: FORCE
	docker run -p 9200:9200 -p 9300:9300 --rm -d --name elasticsearch -e "discovery.type=single-node" \
		--mount type=bind,source=$$(pwd)/data,target=/usr/share/elasticsearch/data \
		docker.elastic.co/elasticsearch/elasticsearch:7.5.1
	docker run -d --name kibana --rm --link elasticsearch:elasticsearch -p 5601:5601 docker.elastic.co/kibana/kibana:7.5.1

stop-db: FORCE
	docker kill elasticsearch kibana

start-ui: FORCE
	cd frontend && ng serve --open

start-api: FORCE
	cd api && npm start
