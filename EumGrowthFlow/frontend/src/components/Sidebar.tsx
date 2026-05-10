import type { DragEvent } from 'react';

interface NodeType {
  type: string;
  label: string;
  agentType: string;
  description: string;
  icon: string;
}

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

const Sidebar = () => {
  const onDragStart = (event: DragEvent, nodeType: NodeType) => {
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
        {nodeTypes.map((node) => (
          <div
            key={`${node.agentType}-${node.label}`}
            draggable
            onDragStart={(e) => onDragStart(e, node)}
            style={{
              padding: '12px',
              backgroundColor: '#fff',
              border: '1px solid #d1d5db',
              borderRadius: '6px',
              cursor: 'grab',
              transition: 'all 0.2s',
            }}
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
