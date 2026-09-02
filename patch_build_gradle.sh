#!/bin/bash

set -e

FILE="build.gradle"
BACKUP="build.gradle.backup-before-sbw"

if [ ! -f "$FILE" ]; then
    echo "ERROR: build.gradle が見つかりません。"
    exit 1
fi

echo "=== SuperbWarfare 0.8.9-final 依存関係パッチ ==="

if [ ! -f "$BACKUP" ]; then
    cp "$FILE" "$BACKUP"
    echo "バックアップ作成: $BACKUP"
else
    echo "バックアップは既に存在します: $BACKUP"
fi

python3 <<'PY'
from pathlib import Path

path = Path("build.gradle")
text = path.read_text(encoding="utf-8")

repo = """    maven {
        name = 'CurseMaven'
        url = 'https://cursemaven.com/'
        content {
            includeGroup 'curse.maven'
        }
    }
"""

dep = """    implementation fg.deobf("curse.maven:superb-warfare-1218165:8104849")
"""

# CurseMaven repository
if "name = 'CurseMaven'" not in text:
    marker = "repositories {"
    pos = text.find(marker)

    if pos == -1:
        raise SystemExit("ERROR: repositories ブロックが見つかりません。")

    # repositories { の直後へ追加
    insert_pos = pos + len(marker)
    text = text[:insert_pos] + "\n\n" + repo + text[insert_pos:]
    print("✓ CurseMaven repository を追加しました")
else:
    print("✓ CurseMaven repository は既に存在します")

# SuperbWarfare dependency
if "curse.maven:superb-warfare-1218165:8104849" not in text:
    marker = 'dependencies {'
    pos = text.find(marker)

    if pos == -1:
        raise SystemExit("ERROR: dependencies ブロックが見つかりません。")

    insert_pos = pos + len(marker)
    text = text[:insert_pos] + "\n\n" + dep + text[insert_pos:]
    print("✓ SuperbWarfare 0.8.9-final dependency を追加しました")
else:
    print("✓ SuperbWarfare dependency は既に存在します")

path.write_text(text, encoding="utf-8")

print("")
print("=== パッチ完了 ===")
PY

echo ""
echo "build.gradle の加工が完了しました。"
echo "バックアップ: $BACKUP"
echo ""
echo "次に IntelliJ IDEA で Gradle を再同期してください。"
