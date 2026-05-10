//
// Sidebar — 드래그 앤 드롭 노드 팔레트
//
// Java 비교: JToolBar / JPanel 기반의 도구 팔레트.
//           ToolPalette palette = new ToolPalette();
//           palette.addTool(new ToolIcon("LLM Agent", () -> createLLMNode()));
//

import type { DragEvent } from 'react';

//
// NodeType 인터페이스
// Java: record NodeType(String type, String label, String agentType, ...) { }
//
interface NodeType {
  type: string;
  label: string;
  agentType: string;
  description: string;
  icon: string;
}

//
// 팔레트에 표시할 노드 타입 목록 (정적 상수)
// Java: private static final List<NodeType> NODE_TYPES = List.of(
//           new NodeType("aiAgent", "LLM Agent", "llm", "AI 언어 모델 노드", "🤖"),
//           ...
//       );
//
const nodeTypes: NodeType[] = [
  {
    type: 'aiAgent',
    label: 'LLM Agent',
    agentType: 'llm',
    description: 'AI 언어 모델 노드',
    icon: '🤖',
  },
  {
    type: 'aiAgent',
    label: 'Tool Node',
    agentType: 'tool',
    description: '도구 실행 노드',
    icon: '🔧',
  },
  {
    type: 'aiAgent',
    label: 'Condition',
    agentType: 'condition',
    description: '조건 분기 노드',
    icon: '🔀',
  },
  {
    type: 'aiAgent',
    label: 'Input',
    agentType: 'input',
    description: '입력 노드',
    icon: '📥',
  },
  {
    type: 'aiAgent',
    label: 'Output',
    agentType: 'output',
    description: '출력 노드',
    icon: '📤',
  },
];

//
// Sidebar 컴포넌트
// Java: public class Sidebar extends JPanel { ... }
//
const Sidebar = () => {
  //
  // onDragStart: HTML5 Drag and Drop API 를 통해 데이터를 설정한다.
  // Java 비교:
  //   TransferHandler handler = new TransferHandler() {
  //       protected Transferable createTransferable(JComponent c) {
  //           return new StringSelection(nodeType.type);
  //       }
  //   };
  //   component.setTransferHandler(handler);
  //
  const onDragStart = (event: DragEvent, nodeType: NodeType) => {
    // dataTransfer: 드래그 중인 데이터를 담는 컨테이너
    // Java: Clipboard / Transferable 에 해당
    event.dataTransfer.setData('application/reactflow', nodeType.type);
    event.dataTransfer.setData('label', nodeType.label);
    event.dataTransfer.setData('agentType', nodeType.agentType);
    event.dataTransfer.setData('description', nodeType.description);
    event.dataTransfer.setData('icon', nodeType.icon);
    event.dataTransfer.effectAllowed = 'move';
  };

  return (
    <aside
      style={{
        width: '250px',
        padding: '20px',
        backgroundColor: '#f9fafb',
        borderRight: '1px solid #e5e7eb',
        overflowY: 'auto',
      }}
    >
      <h2 style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '16px', color: '#1a192b' }}>
        노드 팔레트
      </h2>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {/*
          nodeTypes.map(node => <div key={...}>...</div>)
          Java: nodeTypes.stream().map(node -> renderNodeItem(node)).toList();

          key prop: React 가 리스트 렌더링 시 각 항목을 식별하는 고유값.
          Java: HashMap 의 키 개념. key 가 없으면 React 가 어떤 항목이
                변경되었는지 알 수 없어 비효율적으로 전체를 다시 그린다.
        */}
        {nodeTypes.map((node) => (
          <div
            key={`${node.agentType}-${node.label}`}
            draggable                          // HTML5 draggable 속성
            onDragStart={(e) => onDragStart(e, node)}
            style={{
              padding: '12px',
              backgroundColor: '#fff',
              border: '1px solid #d1d5db',
              borderRadius: '6px',
              cursor: 'grab',
              transition: 'all 0.2s',
            }}
            // onMouseEnter/Leave: 인라인 이벤트로 hover 효과 구현
            // Java: component.addMouseListener(new MouseAdapter() {
            //           public void mouseEntered(MouseEvent e) { ... }
            //           public void mouseExited(MouseEvent e) { ... }
            //       });
            onMouseEnter={(e) => {
              e.currentTarget.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)';
              e.currentTarget.style.transform = 'translateY(-2px)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.boxShadow = 'none';
              e.currentTarget.style.transform = 'translateY(0)';
            }}
          >
            <div style={{ fontSize: '24px', marginBottom: '4px' }}>{node.icon}</div>
            <div style={{ fontSize: '14px', fontWeight: '600', color: '#1a192b', marginBottom: '4px' }}>
              {node.label}
            </div>
            <div style={{ fontSize: '12px', color: '#6b7280' }}>{node.description}</div>
          </div>
        ))}
      </div>

      <div style={{ marginTop: '30px', padding: '15px', backgroundColor: '#eff6ff', borderRadius: '6px' }}>
        <h3 style={{ fontSize: '14px', fontWeight: '600', color: '#1e40af', marginBottom: '8px' }}>
          💡 사용 방법
        </h3>
        <ul style={{ fontSize: '12px', color: '#1e40af', paddingLeft: '20px', lineHeight: '1.6' }}>
          <li>노드를 드래그하여 캔버스에 추가</li>
          <li>노드를 연결하여 워크플로우 생성</li>
          <li>저장 버튼으로 워크플로우 저장</li>
        </ul>
      </div>
    </aside>
  );
};

export default Sidebar;
