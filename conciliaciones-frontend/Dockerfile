FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
ARG FRONTEND_API_AUTH_URL=http://localhost:9081/api/v1
ARG FRONTEND_API_CORE_URL=http://localhost:9091/api/v1
ARG FRONTEND_API_FILE_MANAGEMENT_URL=http://localhost:9092/api/v1
RUN printf "export const environment = {\n  api: {\n    auth: '%s',\n    core: '%s',\n    fileManagement: '%s'\n  }\n};\n" "$FRONTEND_API_AUTH_URL" "$FRONTEND_API_CORE_URL" "$FRONTEND_API_FILE_MANAGEMENT_URL" > src/environments/environment.ts
RUN npm run build -- --configuration production
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/conciliaciones-frontend/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
