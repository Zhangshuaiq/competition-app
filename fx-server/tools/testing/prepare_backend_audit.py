"""Create disposable fixtures for BackendConfigAuditTest; never writes the source database."""
import pathlib
import shutil
import sqlite3

source = pathlib.Path(__file__).resolve().parents[2]
destination = pathlib.Path('/private/tmp/fx-backend-audit')
destination.mkdir(parents=True, exist_ok=True)
with sqlite3.connect((source / 'competition.db').as_uri() + '?mode=ro', uri=True) as live:
    with sqlite3.connect(destination / 'baseline.db') as backup:
        live.backup(backup)
shutil.copytree(source / '资源', destination / '资源', dirs_exist_ok=True)
print('Prepared isolated database and resources:', destination)
