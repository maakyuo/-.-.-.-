# AYN Thor 배경화면 변경

Android GIF 라이브 배경화면 앱 초기 버전.

## 구현
- 저장소에서 GIF 선택 후 앱 내부에 복사
- WallpaperService를 이용해 GIF 반복 재생
- 안드로이드 시스템 배경화면 설정 화면 실행
- GitHub Actions 디버그 APK 빌드

## 제한 사항
- 현재는 하나의 GIF만 라이브 배경화면으로 설정합니다.
- AYN Thor 상단/하단 디스플레이 및 잠금 화면에 서로 다른 GIF를 적용하는 기능은 **미구현**입니다. 기기의 디스플레이/런처/잠금화면 정책 검증이 필요합니다.
- 앱이 이미 실행 중일 때 GIF를 바꾸면 라이브 배경화면을 다시 적용해야 할 수 있습니다.

## 빌드
GitHub Actions의 Build Android APK 실행 후 Artifacts에서 APK 다운로드.
