FROM node:20-alpine AS build
WORKDIR /app
COPY package.json .
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY nginx.conf.template /etc/nginx/conf.d/default.conf.template
COPY --from=build /app/dist /usr/share/nginx/html
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENV API_URL=http://localhost:5000
EXPOSE 80
CMD ["/entrypoint.sh"]
