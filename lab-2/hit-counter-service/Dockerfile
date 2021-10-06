FROM node:16-alpine
COPY ./ $HOME/hit-counter
WORKDIR $HOME/hit-counter
ENV HIT_COUNTER_PORT 80
RUN npm install
CMD npm start
