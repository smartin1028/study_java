import { memo, useState } from 'react';
import { Handle, Position, useReactFlow } from 'reactflow';

interface AIAgentNodeData {
  label: string;
  agentType: string;
  description?: string;
  icon?: string;
}

interface AIAgentNodeProps {
  id: string;
  data: AIAgentNodeData;
}

const AIAgentNode = ({ id, data }: AIAgentNodeProps) => {
  const [isHovered, setIsHovered] = useState(false);
  const { deleteElements } = useReactFlow();

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

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    deleteElements({ nodes: [{ id }] });
  };

  return (
    <div
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      style={{
        padding: '15px',
        borderRadius: '8px',
        border: `2px solid ${getNodeColor(data.agentType)}`,
        backgroundColor: '#fff',
        minWidth: '180px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
        position: 'relative',
      }}
    >
      <Handle type="target" position={Position.Top} style={{ background: '#555' }} />

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

      <Handle type="source" position={Position.Bottom} style={{ background: '#555' }} />
    </div>
  );
};

export default memo(AIAgentNode);
