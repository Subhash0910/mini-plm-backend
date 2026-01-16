# 🔧 Git Swap File & Merge Issue Resolution

**Issue**: Git swap file `.git/.MERGE_MSG.swp` exists from crashed merge process  
**Status**: FIXABLE - Follow steps below to resolve  
**Time**: 2 minutes

---

## 🚀 QUICK FIX (Do This First)

### Option 1: Clean Reset (RECOMMENDED - No Data Loss)

```bash
# Navigate to project
cd /c/mini-plm/mini-plm-backend

# Step 1: Abort any ongoing merge
git merge --abort

# Step 2: Clean up swap files
rm -f .git/.MERGE_MSG.swp
rm -f .git/MERGE_MSG
rm -f .git/MERGE_HEAD

# Step 3: Verify clean state
git status
# Should show: "On branch main, nothing to commit, working tree clean"

# Step 4: Force pull latest (overwrites local, takes GitHub version)
git fetch origin main
git reset --hard origin/main

# Step 5: Verify you're on latest
git log --oneline -5
# Should show latest commits
```

---

## ✅ Detailed Fix Steps

### Step 1: Stop Any Running Git Process

```bash
# PowerShell (Windows)
Get-Process vim, gvim, nvim, nano | Stop-Process -Force

# Or in Git Bash:
killall vim 2>/dev/null || true
killall gvim 2>/dev/null || true
```

### Step 2: Remove Swap Files

```bash
# List all swap files
ls -la /c/mini-plm/mini-plm-backend/.git/

# Remove swap file
rm -f /c/mini-plm/mini-plm-backend/.git/.MERGE_MSG.swp

# Verify it's gone
ls -la /c/mini-plm/mini-plm-backend/.git/ | grep .swp
# Should show: (no output = success)
```

### Step 3: Abort Merge

```bash
# Stop the merge process
git merge --abort

# Or if merge is stuck:
git reset --hard HEAD
```

### Step 4: Clean Git State

```bash
# Remove merge artifacts
rm -f .git/MERGE_MSG
rm -f .git/MERGE_HEAD
rm -f .git/MERGE_MODE

# Clean any untracked files
git clean -fd
```

### Step 5: Verify Status

```bash
# Check current state
git status

# Expected output:
# On branch main
# Your branch is up to date with 'origin/main'.
# nothing to commit, working tree clean
```

### Step 6: Reset to Latest Remote

```bash
# Fetch latest from GitHub
git fetch origin main

# Reset to remote (discards any local changes)
git reset --hard origin/main

# Verify
git log --oneline -3
```

---

## 🔍 Verify Fix Worked

```bash
# 1. Check Git status
git status
# Should show: "On branch main" + "nothing to commit"

# 2. Check recent commits
git log --oneline -5
# Should show production fixes commits

# 3. List files (no swap files)
ls -la .git/ | grep swp
# Should show: (empty)

# 4. Check current branch
git branch
# Should show: * main
```

---

## 🚀 Now Build & Run

Once Git is clean:

```bash
# Pull latest code
git pull origin main

# Build without errors
mvn clean install -DskipTests

# Expected: BUILD SUCCESS

# Run application
mvn spring-boot:run

# Expected: Application running on port 8080
```

---

## 🆘 If Still Having Issues

### Issue: Swap file still appears

```bash
# Find all swap files
find .git -name "*.swp"

# Delete all swap files
find .git -name "*.swp" -delete

# Verify
find .git -name "*.swp"
# Should show: (no output)
```

### Issue: Can't abort merge

```bash
# Force hard reset
git reset --hard HEAD

# Or reset to specific commit (all production fixes in main)
git reset --hard origin/main
```

### Issue: .git folder corrupted

```bash
# Clone fresh copy
cd ..
rm -rf mini-plm-backend
git clone https://github.com/Subhash0910/mini-plm-backend.git
cd mini-plm-backend
```

---

## 📋 Complete Recovery Checklist

- [ ] Stop any running Git editors (vim, nano, gvim)
- [ ] Remove all .swp files: `rm -f .git/.*.swp`
- [ ] Abort merge: `git merge --abort`
- [ ] Reset to remote: `git reset --hard origin/main`
- [ ] Verify status: `git status` (should be clean)
- [ ] Check logs: `git log --oneline -3`
- [ ] No .swp files: `find .git -name "*.swp"` (empty output)
- [ ] Build: `mvn clean install -DskipTests`
- [ ] Run: `mvn spring-boot:run`
- [ ] Test: `curl http://localhost:8080/api/health`

---

## 💡 Prevent Future Issues

### Configure Git to avoid swap files

```bash
# Edit .gitignore
echo "*.swp" >> .gitignore
echo "*.swo" >> .gitignore
echo ".*.swp" >> .gitignore

# Commit
git add .gitignore
git commit -m "chore: ignore vim swap files"
git push origin main
```

### Use safer editors

```bash
# Configure Git to use safe editor
git config --global core.editor "nano"

# Or use VS Code
git config --global core.editor "code --wait"
```

### Disable automatic backups

```bash
# Configure vim to not create swap files
echo 'set noswapfile' >> ~/.vimrc
echo 'set nobackup' >> ~/.vimrc
```

---

## 🎯 Expected Result

After following these steps:

```
✅ Swap files removed
✅ Merge aborted
✅ Git state clean
✅ On branch: main
✅ Latest commits: visible
✅ No merge conflicts
✅ Ready to build & deploy
```

---

## 📞 Next Steps

1. ✅ Follow **Quick Fix (Option 1)** above
2. ✅ Verify with `git status` (should be clean)
3. ✅ Run `mvn clean install -DskipTests`
4. ✅ Run `mvn spring-boot:run`
5. ✅ Test with `curl http://localhost:8080/api/health`

**All production fixes are already in GitHub main branch. This just cleans up your local Git state.**

---

**Status**: Fixable  
**Severity**: Low (local only, doesn't affect GitHub)  
**Recovery Time**: 2 minutes  
**Data Loss Risk**: None with these instructions  

