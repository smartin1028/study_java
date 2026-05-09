# 프로젝트 분석

## 1. 주요 소스 파일 구조
- `src/` 디렉토리:
  - `App.tsx` 및 `main.tsx`: 애플리케이션 엔트리 포인트
  - `components/`: UI 컴포넌트 (채팅 인터페이스, 실행 패널, 노드 구성 패널 등)
  - `services/workflowExecutor.ts`: 워크플로우 실행 로직
  - `utils/encryption.ts`: 암호화 유틸리티

## 2. 자산 파일
- `assets/`:
  - `hero.png` (메인 배너 이미지)
  - 브랜딩 아이콘 (react.svg, vite.svg)

## 3. 미비 사항
- **테스트 커버리지**: `test/` 디렉토리 없음. 확인할 곳:
  - Jest 설정이 있을 수 있음 (custom setup 가능성)
  - `vite.config.ts` 또는 `webpack.config.js` 확인 필요

- **환경 설정 파일**: `config/` 디렉토리 없음. 확인할 곳:
  - `.env` 파일 확인
  - `vite.config.ts` 또는 `tsconfig.json` 검토

- **API 문서**: `docs/` 디렉토리 없음. 확인할 곳:
  - `README.md` 또는 서브디렉토리의 `docs/`
  - 컴포넌트 주석 내 API 참고 사항

- **빌드 구성**: `pom.xml` 없음. 가능성이 있음:
  - Vite 기반 (vite.svg 존재)
  - `package.json` 확인 (빌드 스크립트)
  - `tsconfig.json` TypeScript 설정 확인

## 4. 아키텍처 요약
React 기반 단일 페이지 애플리케이션으로, 채팅 인터페이스, 노드 기반 워크플로우 시각화, 암호화 유틸리티, 기본 UI 요소(사이드바, 모달)를 중심으로 구성되었습니다. 테스트 커버리지와 문서화가 부족한 초기 단계로 보입니다. 빌드 시스템은 Maven이 아닌 Vite 또는 현대적인 프론트엔드 도구로 추정됩니다.

**작성일자**: 2026-05-08