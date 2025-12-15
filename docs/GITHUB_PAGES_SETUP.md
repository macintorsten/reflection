# GitHub Pages Setup Guide

This guide explains how to enable and configure GitHub Pages for the Reflection API documentation.

## Prerequisites

Before deploying to GitHub Pages, ensure:
- You have admin access to the repository
- The workflow file `.github/workflows/deploy-docs.yml` is present in the `main` branch
- The repository settings allow GitHub Actions workflows

## Initial Setup (One-Time)

After merging this PR to the `main` branch, you need to enable GitHub Pages **once**:

### Step 1: Enable GitHub Pages

1. Go to your repository on GitHub
2. Click **Settings** (in the top menu)
3. Scroll down and click **Pages** (in the left sidebar)
4. Under **Source**, select **GitHub Actions**

   ![GitHub Pages Source Selection](https://docs.github.com/assets/cb-47267/images/help/pages/select-github-actions-source.png)

5. Click **Save** (if prompted)

### Step 2: Verify Deployment

1. After the first push to `main` (or manually trigger via Actions tab)
2. The workflow will automatically run and deploy
3. Once complete, your documentation will be available at:
   
   **https://macintorsten.github.io/reflection/**

### Step 3: Check Deployment Status

You can monitor deployments:
- Go to **Actions** tab
- Look for "Deploy API Documentation" workflow runs
- Check the deployment status in the **Environments** section

## How It Works

### Automatic Updates

The documentation automatically updates when changes are pushed to `main` that affect:
- Source code (`src/**`)
- Maven configuration (`pom.xml`)
- Documentation files (`docs/**`)
- Deployment workflow (`.github/workflows/deploy-docs.yml`)

### Manual Deployment

You can manually trigger deployment:
1. Go to **Actions** tab
2. Select **Deploy API Documentation** workflow
3. Click **Run workflow**
4. Select `main` branch and click **Run workflow**

## The Deployment Process

When triggered, the workflow:

1. **Checks out code** from the repository
2. **Sets up Java 21** environment
3. **Generates OpenAPI spec** by:
   - Building the Spring Boot application
   - Starting PostgreSQL database
   - Starting the application
   - Fetching spec from `/v3/api-docs` endpoint
   - Formatting and saving as `docs/openapi.json`
4. **Deploys to GitHub Pages** with:
   - Static HTML page (`docs/index.html`)
   - Generated OpenAPI spec (`docs/openapi.json`)

## Troubleshooting

### Documentation Not Updating

1. Check if the workflow ran successfully in the **Actions** tab
2. Verify the changes you pushed match the `paths` filter in the workflow
3. Check for errors in the workflow logs

### 404 Error on GitHub Pages URL

1. Ensure GitHub Pages is enabled (see Step 1)
2. Wait a few minutes after first deployment
3. Check if the workflow completed successfully
4. Verify the URL format: `https://{username}.github.io/{repo-name}/`

### Build Failures

If the workflow fails during spec generation:
1. Check the application builds locally: `mvn clean package`
2. Ensure database starts correctly: `docker compose up`
3. Verify the application exposes `/v3/api-docs` endpoint
4. Check workflow logs for specific error messages

## Architecture Benefits

This automated approach ensures:
- ✅ **No manual updates** - Documentation stays in sync automatically
- ✅ **No committed artifacts** - `openapi.json` is generated, not stored
- ✅ **Single source of truth** - API annotations drive documentation
- ✅ **Easy to test locally** - Use provided scripts to preview
- ✅ **CI/CD integrated** - Deployments happen automatically

## Additional Resources

- [GitHub Pages Documentation](https://docs.github.com/en/pages)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
