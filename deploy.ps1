<#
.SYNOPSIS
    Build + deploy the Learning Hub to Azure Container Apps (personal subscription).

.DESCRIPTION
    Runs `az containerapp up --source .` (cloud ACR build from the root Dockerfile) and ALWAYS
    passes every required env var / secretref so a deploy can never silently drop one and break
    the app. The three secrets (progress-cs, admin-pw, admin-email) must already exist on the
    Container App — this script references them, it does not create them. To (re)create a secret:

        az containerapp secret set -n learning-hub -g learning-hub-rg --secrets "admin-pw=<value>"

    Prerequisites: Azure CLI logged in to the PERSONAL account (device-code), containerapp
    extension installed. Run from the repository root (the folder containing this script).

.NOTES
    Personal subscription only — never the corp tenant.
#>

$ErrorActionPreference = 'Stop'

# --- Fixed deployment coordinates (personal account) ---------------------------------------
$Subscription = '94846345-a64b-43d5-921f-68077aed64fe'   # personal (asbandopadhyay@gmail.com)
$ResourceGroup = 'learning-hub-rg'
$AppName       = 'learning-hub'
$Environment   = 'learning-hub-env'
$TargetPort    = 8080

# --- Required env vars. Secrets are referenced via `secretref:<name>` and must already exist. --
# HUB_JUDGE_PYTHON_EXE           : python interpreter inside the image.
# HUB_PROGRESS_CONNECTION_STRING : Azure Table storage conn string  -> secret 'progress-cs'.
# HUB_AUTH_ADMIN_PASSWORD        : admin login password             -> secret 'admin-pw'.
# HUB_AUTH_ADMIN_EMAIL           : admin login email                -> secret 'admin-email'.
# HUB_AUTH_COOKIE_SECRET         : HMAC secret for remember-me cookie -> secret 'auth-cookie-secret'.
#                                  MUST stay stable across deploys, or all logins are invalidated.
$EnvVars = @(
    'HUB_JUDGE_PYTHON_EXE=python3',
    'HUB_PROGRESS_CONNECTION_STRING=secretref:progress-cs',
    'HUB_AUTH_ADMIN_PASSWORD=secretref:admin-pw',
    'HUB_AUTH_ADMIN_EMAIL=secretref:admin-email',
    'HUB_AUTH_COOKIE_SECRET=secretref:auth-cookie-secret'
)

Write-Host "==> Selecting personal subscription $Subscription" -ForegroundColor Cyan
az account set --subscription $Subscription

Write-Host "==> Deploying $AppName (cloud build from ./Dockerfile) ..." -ForegroundColor Cyan
az containerapp up `
    --name $AppName `
    --resource-group $ResourceGroup `
    --environment $Environment `
    --source . `
    --ingress external `
    --target-port $TargetPort `
    --env-vars @EnvVars

Write-Host "==> Deploy finished." -ForegroundColor Green
$fqdn = az containerapp show -n $AppName -g $ResourceGroup --query "properties.configuration.ingress.fqdn" -o tsv
Write-Host "Live: https://$fqdn" -ForegroundColor Green
