cd "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "Commit message:"
git add .
read commit
git commit -m "$commit"
git push -u origin master
