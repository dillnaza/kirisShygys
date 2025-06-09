services:
        - type: web
        name: kiris-shygys
        env: docker
        region: frankfurt
        plan: free
        envVars:
        - key: JWT_SECRET
        value: your-secret-key
        - key: DATABASE_URL
        value: your-database-url
