# 👁 Fix Git Swap File - Windows PowerShell / CMD

**Issue**: You're in Windows CMD/PowerShell (not Git Bash), so Linux commands don't work  
**Solution**: Use Windows-specific commands below

---

## 🚀 QUICK FIX FOR WINDOWS (Copy-Paste)

### Option A: PowerShell (Recommended)

```powershell
# 1. Navigate to project
cd C:\mini-plm\mini-plm-backend\mini-plm-backend

# 2. Kill any vim/editor process
Get-Process vim -ErrorAction SilentlyContinue | Stop-Process -Force
Get-Process gvim -ErrorAction SilentlyContinue | Stop-Process -Force
Get-Process nano -ErrorAction SilentlyContinue | Stop-Process -Force

# 3. Abort merge
git merge --abort

# 4. Remove swap files (PowerShell)
Remove-Item .git\.MERGE_MSG.swp -Force -ErrorAction SilentlyContinue
Remove-Item .git\MERGE_MSG -Force -ErrorAction SilentlyContinue
Remove-Item .git\MERGE_HEAD -Force -ErrorAction SilentlyContinue

# 5. Hard reset to main
git fetch origin main
git reset --hard origin/main

# 6. Verify clean
git status

# 7. Build
mvn clean install -DskipTests

# 8. Run
mvn spring-boot:run
```

---

### Option B: Command Prompt (CMD)

```cmd
REM 1. Navigate to project
cd C:\mini-plm\mini-plm-backend\mini-plm-backend

REM 2. Kill vim process
taskkill /IM vim.exe /F 2>nul
taskkill /IM gvim.exe /F 2>nul

REM 3. Abort merge
git merge --abort

REM 4. Remove swap files
del .git\.MERGE_MSG.swp /F /Q 2>nul
del .git\MERGE_MSG /F /Q 2>nul
del .git\MERGE_HEAD /F /Q 2>nul

REM 5. Hard reset
git fetch origin main
git reset --hard origin/main

REM 6. Verify
git status

REM 7. Build
mvn clean install -DskipTests

REM 8. Run
mvn spring-boot:run
```

---

## ✅ Step-by-Step Explanation

### Step 1: Kill Editor Processes

**PowerShell**:
```powershell
Get-Process vim -ErrorAction SilentlyContinue | Stop-Process -Force
```
- `Get-Process vim` = Find vim process
- `-ErrorAction SilentlyContinue` = Don't error if not running
- `Stop-Process -Force` = Kill it forcefully

**CMD**:
```cmd
taskkill /IM vim.exe /F 2>nul
```
- `/IM vim.exe` = Image name (process)
- `/F` = Force kill
- `2>nul` = Hide errors

### Step 2: Abort Merge

```bash
git merge --abort
```
- Stops any ongoing merge process
- Works in both PowerShell and CMD (Git commands are cross-platform)

### Step 3: Remove Swap Files

**PowerShell**:
```powershell
Remove-Item .git\.MERGE_MSG.swp -Force -ErrorAction SilentlyContinue
```

**CMD**:
```cmd
del .git\.MERGE_MSG.swp /F /Q 2>nul
```

### Step 4: Reset Git State

```bash
git fetch origin main
git reset --hard origin/main
```
- `fetch` = Get latest from GitHub
- `reset --hard` = Discard all local changes, use GitHub version

### Step 5: Verify

```bash
git status
```

**Expected output**:
```
On branch main
Your branch is up to date with 'origin/main'.
nothing to commit, working tree clean
```

### Step 6: Build & Run

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

---

## 🌟 If Still Having Issues

### Can't find file to delete?

**PowerShell** - List files first:
```powershell
ls .git -Force | grep -i swap
ls .git -Force | grep -i merge
```

**CMD** - List files first:
```cmd
dir .git /a /s | find "swap"
dir .git /a /s | find "MERGE"
```

### Still stuck on merge?

```bash
# Nuclear option - reset everything
git reset --hard HEAD
git clean -fd
```

### .git folder corrupted?

```bash
# Clone fresh copy
cd ..
rmdir /s mini-plm-backend  (CMD)
rm -rf mini-plm-backend    (PowerShell)
git clone https://github.com/Subhash0910/mini-plm-backend.git
cd mini-plm-backend
```

---

## 📋 Windows Checklist

- [ ] Opened Command Prompt or PowerShell
- [ ] Navigated to: `C:\mini-plm\mini-plm-backend\mini-plm-backend`
- [ ] Killed vim/editor process
- [ ] Ran: `git merge --abort`
- [ ] Deleted swap files: `.git\.MERGE_MSG.swp`
- [ ] Ran: `git fetch origin main`
- [ ] Ran: `git reset --hard origin/main`
- [ ] Verified: `git status` shows clean
- [ ] Built: `mvn clean install -DskipTests`
- [ ] Ran: `mvn spring-boot:run`

---

## 🚀 Expected Output

```
C:\mini-plm\mini-plm-backend\mini-plm-backend>git status
On branch main
Your branch is up to date with 'origin/main'.
nothing to commit, working tree clean

C:\mini-plm\mini-plm-backend\mini-plm-backend>mvn clean install -DskipTests
...
[INFO] BUILD SUCCESS
...

C:\mini-plm\mini-plm-backend\mini-plm-backend>mvn spring-boot:run
...
  .   ____          _            __ _ _
 /\\\ / ___'_ __ _ _(_)_ __ __ _ \ \ \ \\
( ( )___ | '_ | '_| | '_ \/ _` | \ \ \ \\
 \\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.15)

2026-01-16 15:58:00.000  INFO 1234 --- [main] com.sam.Application : Starting Application
2026-01-16 15:58:05.000  INFO 1234 --- [main] com.sam.Application : Started Application in 5.234 seconds
✅ Application running on http://localhost:8080
```

---

## 📞 Next

1. Copy the **PowerShell** or **CMD** commands from above
2. Open your Command Prompt or PowerShell
3. Paste and run
4. When you see `git status` showing "nothing to commit, working tree clean" → SUCCESS
5. Then run `mvn spring-boot:run`
6. Test with: `curl http://localhost:8080/api/health` (in another terminal)

---

**Windows-Specific Notes**:
- Use `\` instead of `/` for Windows paths
- `.swp` swap files live in `.git\` folder
- `taskkill` is Windows Task Manager command
- Git commands work the same in PowerShell and CMD

**Status**: Production fixes already in GitHub. Just cleaning local Git state. ✅

