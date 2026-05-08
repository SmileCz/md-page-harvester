# Portainer

Stack definice je v `deploy/portainer/stack.yml`.

## Ocekavany postup

1. Sestavit Docker image:

   ```bash
   docker build -t md-page-harvester:latest .
   ```

2. Image nahrat do registry, pokud Portainer bezi na jinem hostu.

3. V Portaineru vytvorit stack z `deploy/portainer/stack.yml`.

4. Nastavit promenne:

   ```text
   IMAGE_NAME=md-page-harvester:latest
   HOST_PORT=8080
   SOURCE_PROJECT_URI=
   SOURCE_PROJECT_BRANCH=main
   ```

