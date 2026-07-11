# ---- Build stage ------------------------------------------------------------
FROM node:20-alpine AS build

WORKDIR /workspace

COPY package.json ./
COPY yarn.lock* ./

RUN yarn install --frozen-lockfile --network-timeout 300000 || yarn install --network-timeout 300000

COPY . .
# The dev server proxies /api → backend; in production Nginx does the same,
# so we can build without a specific backend URL baked in.
RUN yarn build

# ---- Runtime stage ----------------------------------------------------------
FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --retries=6 \
  CMD wget --spider -q http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
