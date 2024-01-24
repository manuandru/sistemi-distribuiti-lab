FROM node:16-alpine
COPY ./ $HOME/hit-counter2
WORKDIR $HOME/hit-counter2
ENV HIT_COUNTER_PORT 80
ENV MONGO_USER admin
ENV MONGO_PASSWORD admin
ENV MONGO_HOST localhost
ENV MONGO_PORT 27017
RUN npm install
CMD npm start
