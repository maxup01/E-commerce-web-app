FROM node:22-alpine

COPY package.json .

RUN npm install

COPY src src
COPY *.js .

RUN npm run build

EXPOSE 8000

ENTRYPOINT ["npm", "run", "start"]