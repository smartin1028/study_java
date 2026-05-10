//
// AIAgentNode — ReactFlow 캔버스에 표시되는 커스텀 노드 컴포넌트
//
// Java 비교: JPanel 을 상속한 커스텀 UI 위젯.
//           class AIAgentNodeWidget extends JPanel {
//               private final String id;
//               private final NodeData data;
//               public AIAgentNodeWidget(String id, NodeData data) { ... }
//           }
//
// React 와 Java 의 컴포넌트 모델 차이:
// ┌──────────────────────┬─────────────────────────────────────────────────┐
// │ React                │ Java (Swing)                                    │
// ├──────────────────────┼─────────────────────────────────────────────────┤
// │ 함수형 컴포넌트        │ JPanel 상속 클래스                              │
// │ Props (불변)          │ 생성자 파라미터                                 │
// │ useState (가변 상태)   │ private 필드                                   │
// │ memo() → 불필요 리렌더링 방지 │ 데이터 변경 없으면 paintComponent() 스킵 │
// │ useReactFlow()       │ @Autowired MainFrame frame;                     │
// │ 이벤트 핸들러 (인라인)  │ addActionListener / addMouseListener           │
// └──────────────────────┴─────────────────────────────────────────────────┘
//

import { memo, useState } from 'react';
import { Handle, Position, useReactFlow } from 'reactflow';

//
// 노드 데이터 구조체
// Java: record AIAgentNodeData(String label, String agentType, ...) { }
//
interface AIAgentNodeData {
  label: string;
  agentType: string;
  description?: string;
  icon?: string;
}

//
// ReactFlow 가 커스텀 노드 컴포넌트에 주입하는 Props
// Java: ReactFlow 가 이 인터페이스에 맞춰 데이터를 setProperties() 로 주입.
//
interface AIAgentNodeProps {
  id: string;                      // ReactFlow 가 자동 주입하는 노드 ID
  data: AIAgentNodeData;           // 사용자 정의 데이터 (노드 생성 시 data 필드)
}

const AIAgentNode = ({ id, data }: AIAgentNodeProps) => {
  //
  // useState(false): 마우스 호버 여부 → 삭제 버튼 표시 제어
  // Java: private boolean isHovered = false;
  //
  const [isHovered, setIsHovered] = useState(false);

  //
  // useReactFlow(): ReactFlow 의 내부 API 에 접근하는 훅
  // Java: @Autowired private ReactFlowInstance reactFlowInstance;
  //
  const { deleteElements } = useReactFlow();

  //
  // agentType 별 경계선 색상 매핑
  // Java: private static final Map<String, Color> COLOR_MAP = Map.of(
  //           "input", new Color(0x10b981), "llm", new Color(0x3b82f6), ...);
  //       Color getNodeColor(String type) { return COLOR_MAP.getOrDefault(type, GRAY); }
  //
  const getNodeColor = (type: string) => {
    switch (type) {
      case 'input':
        return '#10b981';
      case 'llm':
        return '#3b82f6';
      case 'tool':
        return '#f59e0b';
      case 'output':
        return '#ef4444';
      case 'condition':
        return '#8b5cf6';
      default:
        return '#6b7280';
    }
  };

  //
  // 삭제 버튼 클릭 → ReactFlow 컨텍스트에서 현재 노드 제거
  // e.stopPropagation(): 이벤트 버블링 방지 (노드 선택과 삭제가 동시에 발생하지 않도록)
  // Java: MouseAdapter.mouseClicked(e) { e.consume(); parent.removeNode(id); }
  //
  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    deleteElements({ nodes: [{ id }] });
  };

  //
  // JSX: "HTML 같지만 실제로는 JavaScript 객체를 생성하는 문법"
  // {data.icon && <div>...} → 조건부 렌더링 (Java: if (data.icon != null) { ... })
  // style={{ ... }} → 인라인 스타일 객체 (Java: setBackground(Color.WHITE))
  //

  return (
    <div
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      style={{
        padding: '15px',
        borderRadius: '8px',
        border: `2px solid ${getNodeColor(data.agentType)}`,  // 템플릿 리터럴 (Java: String.format)
        backgroundColor: '#fff',
        minWidth: '180px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
        position: 'relative',
      }}
    >
      {/*
        Handle: ReactFlow 의 연결점 (노드 간 엣지를 연결하는 포트)
        Java 비교: 그래프 편집기의 InputPort / OutputPort 위젯.
                  Position.Top → 포트가 노드 상단에 위치.
        */}
      <Handle type="target" position={Position.Top} style={{ background: '#555' }} />

      {/*
        조건부 렌더링: isHovered 가 true 일 때만 삭제 버튼이 DOM 에 존재한다.
        Java: if (isHovered) { add(deleteButton); } else { remove(deleteButton); }
        React 에서는 상태 변경 시 컴포넌트가 "다시 그려지면서" 자연스럽게 적용된다.
      */}
      {isHovered && (
        <button
          onClick={handleDelete}
          style={{
            position: 'absolute',
            top: '4px',
            right: '4px',
            width: '20px',
            height: '20px',
            border: 'none',
            borderRadius: '4px',
            backgroundColor: '#ef4444',
            color: '#fff',
            fontSize: '12px',
            lineHeight: '20px',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
          title="노드 삭제"
        >
          ✕
        </button>
      )}

      <div style={{ marginBottom: '8px' }}>
        {/* 아이콘이 있을 때만 렌더링 */}
        {data.icon && (
          <div style={{ fontSize: '20px', marginBottom: '4px' }}>{data.icon}</div>
        )}
        <div
          style={{
            fontSize: '11px',
            color: getNodeColor(data.agentType),
            fontWeight: 'bold',
            textTransform: 'uppercase',
            marginBottom: '4px',
          }}
        >
          {data.agentType}
        </div>
        <div style={{ fontSize: '14px', fontWeight: '600', color: '#1a192b' }}>
          {data.label}
        </div>
      </div>

      {data.description && (
        <div style={{ fontSize: '12px', color: '#6b7280', marginTop: '8px' }}>
          {data.description}
        </div>
      )}

      {/*
        Handle type="source": 엣지의 출발점 포트
        Java: OutputPort 출력포트 = new OutputPort(Position.BOTTOM);
      */}
      <Handle type="source" position={Position.Bottom} style={{ background: '#555' }} />
    </div>
  );
};

//
// memo(Component): Props 가 변경되지 않으면 리렌더링을 건너뛴다.
// Java 비교: 데이터 변경이 없으면 paintComponent() 를 호출하지 않는 최적화.
//           또는 @Cacheable 어노테이션으로 결과를 캐싱하는 것.
//
export default memo(AIAgentNode);
